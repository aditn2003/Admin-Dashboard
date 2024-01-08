package com.example.graphsdemo;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ChartsDemoController<YourControllerClass> implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Pane paneCentre;


    @FXML
    private TextField keywordTextField;

    @FXML
    private Label searchLabel;

    private static final ArrayList<Double> orderNumbers = new ArrayList<>();
    private static final ArrayList<String> orderMonths = new ArrayList<>();
    private static final ArrayList<Double> prices = new ArrayList<>();
    private static final ArrayList<Double> orderValues = new ArrayList<>();
    @FXML
    private TableView<CustomerSearchModel> customerTableView;

    @FXML
    private TableColumn<CustomerSearchModel, Integer> customerNumberTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> customerNameTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> contactLastNameTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> contactFirstNameTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> phoneTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> addressLine1Tc;
    @FXML
    private TableColumn<CustomerSearchModel, String> addressLine2Tc;
    @FXML
    private TableColumn<CustomerSearchModel, String> cityTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> stateTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> postalCodeTc;
    @FXML
    private TableColumn<CustomerSearchModel, String> countryTc;
    @FXML
    private TableColumn<CustomerSearchModel, Integer> salesRepEmployeeNumberTc;
    @FXML
    private TableColumn<CustomerSearchModel, Double> creditLimitTc;



    ObservableList<CustomerSearchModel> customerSearchModelObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/classicmodels", "root", "Poiuyt@09SQL")) {
            System.out.println("Connected to the database!");
            performDatabaseOperations(connection);
            //handleShowBarChart(null);
            handleCustomerTableView(null);
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void performDatabaseOperations(Connection connection) {

        String orderQuery = "SELECT orderNumber, orderDate FROM orders ORDER BY orderNumber";
        String priceQuery = "SELECT priceEach FROM orderdetails ORDER BY orderNumber";

        try (PreparedStatement orderStatement = connection.prepareStatement(orderQuery);
             ResultSet orderResultSet = orderStatement.executeQuery()) {

            while (orderResultSet.next()) {
                int orderNumber = orderResultSet.getInt("orderNumber");
                String orderDate = orderResultSet.getString("orderDate");
                orderNumbers.add((double) orderNumber);

                String month = orderDate.substring(0, 7);
                if (!orderMonths.contains(month)) {
                    orderMonths.add(month);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error executing order query: " + e.getMessage());
        }

        try (PreparedStatement priceStatement = connection.prepareStatement(priceQuery);
             ResultSet priceResultSet = priceStatement.executeQuery()) {

            while (priceResultSet.next()) {
                double priceEach = priceResultSet.getDouble("priceEach");
                prices.add(priceEach);

                orderValues.add(priceEach);
            }

        } catch (SQLException e) {
            System.err.println("Error executing price query: " + e.getMessage());
        }
        String customerViewQuery = "SELECT customerNumber, customerName, contactLastName, contactFirstName, phone, addressLine1, addressLine2, city, state, postalCode, country, salesRepEmployeeNumber, creditLimit FROM classicmodels.customers";

        try{
            PreparedStatement statement = connection.prepareStatement(customerViewQuery);
            ResultSet queryOutput = statement.executeQuery(customerViewQuery);
            while (queryOutput.next()){

                    Integer queryCustomerNumber = queryOutput.getInt("customerNumber");
                    String queryCustomerName = queryOutput.getString("customerName");
                    String queryContactLastName = queryOutput.getString("contactLastName");
                    String queryContactFirstName = queryOutput.getString("contactFirstName");
                    String queryPhone = queryOutput.getString("phone");
                    String queryAddressLine1 = queryOutput.getString("addressLine1");
                    String queryAddressLine2 = queryOutput.getString("addressLine2");
                    String queryCity = queryOutput.getString("city");
                    String queryState = queryOutput.getString("state");
                    String queryPostalCode = queryOutput.getString("postalCode");
                    String queryCountry = queryOutput.getString("country");
                    Integer querySalesRepEmployeeNumber = queryOutput.getInt("salesRepEmployeeNumber");
                    Double queryCreditLimit = queryOutput.getDouble("creditLimit");




                customerSearchModelObservableList.add(new CustomerSearchModel(queryCustomerNumber, queryCustomerName, queryContactLastName, queryContactFirstName, queryPhone, queryAddressLine1, queryAddressLine2, queryCity, queryState, queryPostalCode, queryCountry, querySalesRepEmployeeNumber, queryCreditLimit) );
            }
            customerNumberTc.setCellValueFactory(new PropertyValueFactory<>("customerNumber"));
            customerNameTc.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            contactLastNameTc.setCellValueFactory(new PropertyValueFactory<>("contactLastName"));
            contactFirstNameTc.setCellValueFactory(new PropertyValueFactory<>("contactFirstName"));
            phoneTc.setCellValueFactory(new PropertyValueFactory<>("phone"));
            addressLine1Tc.setCellValueFactory(new PropertyValueFactory<>("addressLine1"));
            addressLine2Tc.setCellValueFactory(new PropertyValueFactory<>("addressLine2"));
            cityTc.setCellValueFactory(new PropertyValueFactory<>("city"));
            stateTc.setCellValueFactory(new PropertyValueFactory<>("state"));
            postalCodeTc.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            countryTc.setCellValueFactory(new PropertyValueFactory<>("country"));
            salesRepEmployeeNumberTc.setCellValueFactory(new PropertyValueFactory<>("salesRepEmployeeNumber"));
            creditLimitTc.setCellValueFactory(new PropertyValueFactory("creditLimit"));

            customerTableView.setItems(customerSearchModelObservableList);

            FilteredList<CustomerSearchModel> filteredData = new FilteredList<>(customerSearchModelObservableList, b-> true);

            keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(customerSearchModel -> {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();

                    String state = customerSearchModel.getState();
                    if (state != null && state.toLowerCase().contains(searchKeyword)) {
                        return true;}
                    String postalCode = customerSearchModel.getPostalCode();
                    if (postalCode != null && postalCode.toLowerCase().contains(searchKeyword)) {
                        return true;
                    }
                    String addressLine2 = customerSearchModel.getAddressLine2();
                    if (addressLine2 != null && addressLine2.toLowerCase().contains(searchKeyword)) {
                        return true;
                    }
                    Integer salesRepEmployeeNumber = customerSearchModel.getSalesRepEmployeeNumber();

                    if (salesRepEmployeeNumber != null && addressLine2 != null &&
                            salesRepEmployeeNumber.toString().contains(searchKeyword) &&
                            addressLine2.toLowerCase().contains(searchKeyword)) {
                        return true;
                    }

                    if(customerSearchModel.getCustomerName().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getContactFirstName().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getCustomerNumber().toString().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getContactLastName().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getPhone().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getCity().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getCountry().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getCreditLimit().toString().indexOf(searchKeyword) > -1){return true;}
                    else if(customerSearchModel.getAddressLine1().toLowerCase().indexOf(searchKeyword) > -1){return true;}
                    else return false;

                });
            });
            SortedList<CustomerSearchModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(customerTableView.comparatorProperty());
            customerTableView.setItems(sortedData);

        } catch (SQLException ex) {
            throw new RuntimeException(ex);}}


        @FXML
    void handleClose(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    public void handleShowBarChart(ActionEvent event) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Order Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Order Quantity");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> data = new XYChart.Series<>();
        data.setName("Orders");

        for (int c = 0; c < orderMonths.size(); c++) {
            data.getData().add(new XYChart.Data<>(orderMonths.get(c), orderValues.get(c)));
        }

        barChart.getData().add(data);
        borderPane.setCenter(barChart);

        System.out.println("Data added to BarChart: " + data.getData());
    }

    @FXML
    private void handleShowPieChart(ActionEvent event) {
        borderPane.setCenter(setupPieChart());
    }

    private PieChart setupPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (int c = 0; c < orderMonths.size(); c++) {
            pieChartData.add(new PieChart.Data(orderMonths.get(c), orderValues.get(c)));
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Orders by Month");

        ContextMenu contextMenu = new ContextMenu();
        MenuItem miSwitchToBarChart = new MenuItem("Switch To Bar Chart");
        contextMenu.getItems().add(miSwitchToBarChart);

        pieChart.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            contextMenu.show(pieChart, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                        }
                    }
                });

        miSwitchToBarChart.setOnAction((ActionEvent actionEvent) -> {
            handleShowBarChart(null);
        });



        return pieChart;
    }

    @FXML
    void handleUpdateData(ActionEvent event) {
    }

    @FXML
    void handleCustomerTableView(ActionEvent event) {
        paneCentre.getChildren().clear();
        paneCentre.getChildren().addAll(customerTableView, keywordTextField, searchLabel);



    }

}

