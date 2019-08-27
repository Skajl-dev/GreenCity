package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Category;
import greencity.exception.BadCategoryRequestException;
import greencity.exception.NotFoundException;
import greencity.repository.CategoryRepo;
import greencity.service.CategoryService;
import java.util.List;
import greencity.constant.ErrorMessage;
import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import greencity.exception.BadCategoryRequestException;
import greencity.exception.BadIdException;
import greencity.repository.CategoryRepo;
import greencity.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Category entity.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    private ModelMapper modelMapper;

    /**
     * Method for saving Category to database.
     *
     * @param dto - dto for Category entity
     * @return category
     * @author Kateryna Horokh
     */
    @Override
    public Category save(CategoryDto dto) {
        log.info("In save category method");

        Category byName = categoryRepo.findByName(dto.getName());

        if (byName != null) {
            throw new BadCategoryRequestException(
                    ErrorMessage.CATEGORY_ALREADY_EXISTS_BY_THIS_NAME);
        }
        return categoryRepo.save(Category.builder().name(dto.getName()).build());
    }

    /**
     * Find all categories from DB.
     *
     * @return List of categories.
     * @author Nazar Vladyka
     */
    @Override
    public List<Category> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return categoryRepo.findAll();
    }

    /**
     * Find Category entity by id.
     *
     * @param id - Category id.
     * @return Category entity.
     * @author Nazar Vladyka
     */
    @Override
    public Category findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return categoryRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND_BY_ID + id));
    }

    /**
     * Save Category to DB.
     *
     * @param category - entity of Category.
     * @return saved Category.
     * @author Nazar Vladyka
     */
    @Override
    public Category save(Category category) {
        log.info("in save(Category category), {}", category);

        return categoryRepo.saveAndFlush(category);
    }

    /**
     * Update Category in DB.
     *
     * @param id - Category id.
     * @param category - Category entity.
     * @return Category updated entity.
     * @author Nazar Vladyka
     */
    @Override
    public Category update(Long id, Category category) {
        log.info(LogMessage.IN_UPDATE, category);

        Category updatable = findById(id);

        updatable.setName(category.getName());
        updatable.setParentCategory(category.getParentCategory());
        updatable.setCategories(category.getCategories());
        updatable.setPlaces(category.getPlaces());

        return categoryRepo.save(category);
    }

    /**
     * Delete entity from DB by id.
     *
     * @param id - Category id.
     * @author Nazar Vladyka
     */
    @Override
    public void deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        findById(id);

        categoryRepo.deleteById(id);
    }

    /**
     * Find entity from DB by name.
     *
     * @param name - Category name.
     * @author Kateryna Horokh
     */
    @Override
    public Category findByName(String name) {
        return categoryRepo.findByName(name);
    }

    @Override
    public List<CategoryDto> findAllCategoryDto() {
        List<Category> categories = findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }
}