package com.example.employmentcomparisonapplication;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmploymentComparisonController {
    @FXML
    private BarChart<String, Number> employmentRateChart;
    @FXML
    private TableView<Object[]> employmentRateTable;
    @FXML
    private TableColumn<Object[], String> provinceColumn;
    @FXML
    private TableColumn<Object[], Integer> yearColumn;
    @FXML
    private TableColumn<Object[], Double> rateColumn;
    @FXML
    private Button switchViewButton;

    private boolean isChartView = true;
    private DatabaseConnector dbConnector;

    @FXML
    private void initialize() {
        dbConnector = new DatabaseConnector();
        setupTable();
        loadChartData();
        loadTableData();
    }

    private void setupTable() {
        provinceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue()[0]));
        yearColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty((Integer) data.getValue()[1]).asObject());
        rateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty((Double) data.getValue()[2]).asObject());
    }

    private void loadChartData() {
        XYChart.Series<String, Number> seriesAlberta = new XYChart.Series<>();
        seriesAlberta.setName("Alberta");

        XYChart.Series<String, Number> seriesOntario = new XYChart.Series<>();
        seriesOntario.setName("Ontario");

        try (Connection connection = dbConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM employment_rates")) {

            while (resultSet.next()) {
                String province = resultSet.getString("province");
                String year = String.valueOf(resultSet.getInt("year"));
                double employmentRate = resultSet.getDouble("employment_rate");

                if ("Alberta".equals(province)) {
                    seriesAlberta.getData().add(new XYChart.Data<>(year, employmentRate));
                } else if ("Ontario".equals(province)) {
                    seriesOntario.getData().add(new XYChart.Data<>(year, employmentRate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        employmentRateChart.getData().addAll(seriesAlberta, seriesOntario);
    }

    private void loadTableData() {
        try (Connection connection = dbConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM employment_rates")) {

            while (resultSet.next()) {
                employmentRateTable.getItems().add(new Object[]{
                        resultSet.getString("province"),
                        resultSet.getInt("year"),
                        resultSet.getDouble("employment_rate")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchView() {
        if (isChartView) {
            employmentRateChart.setVisible(false);
            employmentRateTable.setVisible(true);
            switchViewButton.setText("Show Chart");
        } else {
            employmentRateChart.setVisible(true);
            employmentRateTable.setVisible(false);
            switchViewButton.setText("Show Table");
        }
        isChartView = !isChartView;
    }
}