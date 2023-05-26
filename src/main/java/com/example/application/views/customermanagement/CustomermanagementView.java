package com.example.application.views.customermanagement;

import com.example.application.data.entity.Customer;
import com.example.application.data.service.CustomerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Customermanagement")
@Route(value = "customermanagement/:customerID?/:action?(edit)", layout = MainLayout.class)
public class CustomermanagementView extends Div implements BeforeEnterObserver {

    private final String CUSTOMER_ID = "customerID";
    private final String CUSTOMER_EDIT_ROUTE_TEMPLATE = "customermanagement/%s/edit";

    private final Grid<Customer> grid = new Grid<>(Customer.class, false);

    private TextField customerId;
    private TextField customerFirm;
    private TextField customerStreet;
    private TextField customerHouseNr;
    private TextField customerZIP;
    private TextField customerCity;
    private TextField customerCountry;
    private TextField customerFirmPhone;
    private TextField customerFirmMail;
    private TextField customerContactPerson;
    private TextField customerContactPhone;
    private TextField customerContactMail;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Customer> binder;

    private Customer customer;

    private final CustomerService customerService;

    public CustomermanagementView(CustomerService customerService) {
        this.customerService = customerService;
        addClassNames("customermanagement-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("customerId").setAutoWidth(true);
        grid.addColumn("customerFirm").setAutoWidth(true);
        grid.addColumn("customerStreet").setAutoWidth(true);
        grid.addColumn("customerHouseNr").setAutoWidth(true);
        grid.addColumn("customerZIP").setAutoWidth(true);
        grid.addColumn("customerCity").setAutoWidth(true);
        grid.addColumn("customerCountry").setAutoWidth(true);
        grid.addColumn("customerFirmPhone").setAutoWidth(true);
        grid.addColumn("customerFirmMail").setAutoWidth(true);
        grid.addColumn("customerContactPerson").setAutoWidth(true);
        grid.addColumn("customerContactPhone").setAutoWidth(true);
        grid.addColumn("customerContactMail").setAutoWidth(true);
        grid.setItems(query -> customerService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CUSTOMER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CustomermanagementView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Customer.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(customerId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("customerId");
        binder.forField(customerHouseNr).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("customerHouseNr");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.customer == null) {
                    this.customer = new Customer();
                }
                binder.writeBean(this.customer);
                customerService.update(this.customer);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(CustomermanagementView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> customerId = event.getRouteParameters().get(CUSTOMER_ID).map(Long::parseLong);
        if (customerId.isPresent()) {
            Optional<Customer> customerFromBackend = customerService.get(customerId.get());
            if (customerFromBackend.isPresent()) {
                populateForm(customerFromBackend.get());
            } else {
                Notification.show(String.format("The requested customer was not found, ID = %s", customerId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CustomermanagementView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        customerId = new TextField("Customer Id");
        customerFirm = new TextField("Customer Firm");
        customerStreet = new TextField("Customer Street");
        customerHouseNr = new TextField("Customer House Nr");
        customerZIP = new TextField("Customer ZIP");
        customerCity = new TextField("Customer City");
        customerCountry = new TextField("Customer Country");
        customerFirmPhone = new TextField("Customer Firm Phone");
        customerFirmMail = new TextField("Customer Firm Mail");
        customerContactPerson = new TextField("Customer Contact Person");
        customerContactPhone = new TextField("Customer Contact Phone");
        customerContactMail = new TextField("Customer Contact Mail");
        formLayout.add(customerId, customerFirm, customerStreet, customerHouseNr, customerZIP, customerCity,
                customerCountry, customerFirmPhone, customerFirmMail, customerContactPerson, customerContactPhone,
                customerContactMail);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Customer value) {
        this.customer = value;
        binder.readBean(this.customer);

    }
}
