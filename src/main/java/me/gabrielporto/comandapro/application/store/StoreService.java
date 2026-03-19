package me.gabrielporto.comandapro.application.store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreHours;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateStoreHoursRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.UploadResult;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class StoreService {

    private final StoreJpaRepository storeRepository;

    public StoreService(StoreJpaRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public Store findBySlug(String slug) {
        return storeRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Store> findAllActive() {
        return storeRepository.findAllByStatus(StoreStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Store findByUserId(UUID userId) {
        return storeRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada para este usuário"));
    }

    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return storeRepository.existsBySlug(slug);
    }

    @Transactional
    public List<StoreHours> updateAllHorarios(UUID storeId, UUID userId,
            List<CreateStoreHoursRequest> requests) {

        Store store = findStoreAndValidateOwner(storeId, userId);
        List<StoreHours> horariosAtualizados = new ArrayList<>();

        for (CreateStoreHoursRequest request : requests) {
            StoreHours existingHorario = store.getHorarios().stream()
                    .filter(h -> h.getDayOfWeek().equals(request.dayOfWeek()))
                    .findFirst()
                    .orElse(null);

            if (existingHorario == null) {
                existingHorario = new StoreHours();
                existingHorario.setStore(store);
                existingHorario.setDayOfWeek(request.dayOfWeek());
                store.addHorario(existingHorario);
            }

            existingHorario.setOpenTime(request.openTime());
            existingHorario.setCloseTime(request.closeTime());
            existingHorario.setIsClosed(request.isClosed() != null ? request.isClosed() : false);
            horariosAtualizados.add(existingHorario);
        }

        storeRepository.save(store);
        return horariosAtualizados;
    }

    @Transactional
    public Store toggleStatus(UUID storeId, UUID userId) {
        Store store = findStoreAndValidateOwner(storeId, userId);

        switch (store.getStatus()) {
            case ACTIVE ->
                store.suspender();
            case SUSPENDED ->
                store.ativar();
            default ->
                throw new BusinessException("Não é possível alterar status de loja cancelada");
        }

        return storeRepository.save(store);
    }

    @Transactional
    public void deleteStore(UUID storeId, UUID userId) {
        Store store = findStoreAndValidateOwner(storeId, userId);
        store.cancelar();
        storeRepository.save(store);
    }

    @Transactional
    public Store updatePhotos(UUID storeId, UUID userId, List<String> photos) {
        Store store = findStoreAndValidateOwner(storeId, userId);
        store.setPhotos(photos);
        return storeRepository.save(store);
    }

    @Transactional
    public Store addPhoto(UUID storeId, UUID userId, String photoUrl) {
        Store store = findStoreAndValidateOwner(storeId, userId);

        List<String> photos = store.getPhotos();
        if (photos == null) {
            photos = new ArrayList<>();
        }

        if (photos.size() >= 3) {
            throw new BusinessException("Máximo de 3 fotos permitidas");
        }

        photos.add(photoUrl);
        store.setPhotos(photos);

        return storeRepository.save(store);
    }

    @Transactional
    public UploadResult uploadPhotos(UUID storeId, UUID userId, List<MultipartFile> files, String baseUrl, String uploadPath) {

        if (files == null || files.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado");
        }

        if (files.size() > 3) {
            throw new BusinessException("Máximo de 3 fotos permitidas");
        }

        files.forEach(this::validateFile);

        try {
            Path uploadDir = Paths.get(uploadPath, storeId.toString());
            Files.createDirectories(uploadDir);

            try (Stream<Path> existing = Files.list(uploadDir)) {
                existing.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                    }
                });
            }

            List<String> photoUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = generateFileName(file);
                Path filePath = uploadDir.resolve(fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String photoUrl = String.format("%s/uploads/%s/%s", baseUrl, storeId, fileName);
                photoUrls.add(photoUrl);
            }

            Store store = updatePhotos(storeId, userId, photoUrls);

            return new UploadResult(photoUrls, store.getPhotos());

        } catch (IOException e) {
            throw new BusinessException("Erro ao salvar arquivos: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Apenas arquivos de imagem são permitidos");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("Arquivo muito grande. Tamanho máximo: 2MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!List.of("jpg", "jpeg", "png", "gif", "webp").contains(extension)) {
                throw new BusinessException("Formato de arquivo não suportado. Use: JPG, PNG, GIF ou WEBP");
            }
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return UUID.randomUUID().toString() + extension;
    }

    @Transactional
    public Store addCategories(UUID storeId, UUID userId, List<String> newCategories) {
        Store store = findStoreAndValidateOwner(storeId, userId);

        List<String> currentCategories = store.getCategories();
        if (currentCategories == null) {
            currentCategories = new ArrayList<>();
        }

        List<String> categoriesToAdd = new ArrayList<>();
        for (String category : newCategories) {
            if (category == null || category.trim().isEmpty()) {
                throw new BusinessException("Nome da categoria não pode ser vazio");
            }

            String trimmedCategory = category.trim();

            if (currentCategories.contains(trimmedCategory)) {
                throw new BusinessException("Categoria '" + trimmedCategory + "' já existe");
            }

            categoriesToAdd.add(trimmedCategory);
        }

        currentCategories.addAll(categoriesToAdd);
        store.setCategories(currentCategories);

        return storeRepository.save(store);
    }

    @Transactional
    public Store removeCategories(UUID storeId, UUID userId, List<String> categoriesToRemove) {
        Store store = findStoreAndValidateOwner(storeId, userId);

        List<String> currentCategories = store.getCategories();
        if (currentCategories == null || currentCategories.isEmpty()) {
            throw new BusinessException("Não há categorias para remover");
        }

        List<String> updatedCategories = new ArrayList<>(currentCategories);

        for (String category : categoriesToRemove) {
            if (category == null || category.trim().isEmpty()) {
                throw new BusinessException("Nome da categoria não pode ser vazio");
            }

            String trimmedCategory = category.trim();
            boolean removed = updatedCategories.remove(trimmedCategory);

            if (!removed) {
                throw new BusinessException("Categoria '" + trimmedCategory + "' não encontrada");
            }
        }

        store.setCategories(updatedCategories);

        return storeRepository.save(store);
    }

    @Transactional
    public Store updatePaymentMethods(UUID storeId, UUID userId, List<PaymentMethod> paymentMethods) {
        Store store = findStoreAndValidateOwner(storeId, userId);

        List<PaymentMethod> sanitized = paymentMethods == null
                ? new ArrayList<>()
                : paymentMethods.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        store.setPaymentMethods(sanitized);
        return storeRepository.save(store);
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories(UUID storeId, UUID userId) {
        Store store = findStoreAndValidateOwner(storeId, userId);

        List<String> categories = store.getCategories();
        return categories != null ? categories : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategoriesPublic(String slug) {
        Store store = findBySlug(slug);

        List<String> categories = store.getCategories();
        return categories != null ? categories : new ArrayList<>();
    }

    private Store findStoreAndValidateOwner(UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));

        if (!store.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para gerenciar esta loja");
        }

        return store;
    }

    protected String generateSlug(String nome) {
        String slug = nome.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");

        String originalSlug = slug;
        int counter = 1;
        while (storeRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}
