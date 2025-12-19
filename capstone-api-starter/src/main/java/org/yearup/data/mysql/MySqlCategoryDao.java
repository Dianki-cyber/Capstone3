package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sqlQuery = "SELECT category_id, name, description FROM categories";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Category category = mapRow(resultSet);
                categories.add(category);
            }

        } catch (SQLException exception) {
            System.out.println("There was a problem with database");
            exception.printStackTrace();
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        String sqlQuery = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException ex) {
            System.out.println("There was a problem with the database");
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Category create(Category category) {
        String sqlQuery = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                }
            }

            return category;

        } catch (SQLException ex) {
            System.out.println("There was a problem creating the category");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(int categoryId, Category category) {
        String sqlQuery = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("There was a problem updating the category");
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(int categoryId) {
        String sqlQuery = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {

            stmt.setInt(1, categoryId);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("There was a problem deleting the category");
            ex.printStackTrace();
        }
    }

    // ✅ mapRow is now INSIDE the class
    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName(name);
        category.setDescription(description);

        return category;
    }
}
