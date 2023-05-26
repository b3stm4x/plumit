package com.example.application.views.ordermanagement;

import com.example.application.data.entity.Order;
import com.example.application.data.service.OrderService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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

@PageTitle("Ordermanagement")
@Route(value = "ordermanagement/:orderID?/:action?(edit)", layout = MainLayout.class)
public class OrdermanagementView extends Div implements BeforeEnterObserver {

    private final String ORDER_ID = "orderID";
    private final String ORDER_EDIT_ROUTE_TEMPLATE = "ordermanagement/%s/edit";

    private final Grid<Order> grid = new Grid<>(Order.class, false);

    private TextField orderId;
    private TextField customerId;
    private TextField objectDescription;
    private TextField pickupAdress;
    private TextField pickupCountry;
    private TextField deliveryAdress;
    private TextField deliveryCountry;
    private TextField vehicleId;
    private DatePicker etd;
    private DatePicker eta;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Order> binder;

    private Order order;

    private final OrderService orderService;

    public OrdermanagementView(OrderService orderService) {
        this.orderService = orderService;
        addClassNames("ordermanagement-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("orderId").setAutoWidth(true);
        grid.addColumn("customerId").setAutoWidth(true);
        grid.addColumn("objectDescription").setAutoWidth(true);
        grid.addColumn("pickupAdress").setAutoWidth(true);
        grid.addColumn("pickupCountry").setAutoWidth(true);
        grid.addColumn("deliveryAdress").setAutoWidth(true);
        grid.addColumn("deliveryCountry").setAutoWidth(true);
        grid.addColumn("vehicleId").setAutoWidth(true);
        grid.addColumn("etd").setAutoWidth(true);
        grid.addColumn("eta").setAutoWidth(true);
        grid.setItems(query -> orderService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ORDER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(OrdermanagementView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Order.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(orderId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("orderId");
        binder.forField(customerId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("customerId");
        binder.forField(vehicleId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("vehicleId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.order == null) {
                    this.order = new Order();
                }
                binder.writeBean(this.order);
                orderService.update(this.order);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(OrdermanagementView.class);
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
        Optional<Long> orderId = event.getRouteParameters().get(ORDER_ID).map(Long::parseLong);
        if (orderId.isPresent()) {
            Optional<Order> orderFromBackend = orderService.get(orderId.get());
            if (orderFromBackend.isPresent()) {
                populateForm(orderFromBackend.get());
            } else {
                Notification.show(String.format("The requested order was not found, ID = %s", orderId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(OrdermanagementView.class);
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
        orderId = new TextField("Order Id");
        customerId = new TextField("Customer Id");
        objectDescription = new TextField("Object Description");
        pickupAdress = new TextField("Pickup Adress");
        pickupCountry = new TextField("Pickup Country");
        deliveryAdress = new TextField("Delivery Adress");
        deliveryCountry = new TextField("Delivery Country");
        vehicleId = new TextField("Vehicle Id");
        etd = new DatePicker("Etd");
        eta = new DatePicker("Eta");
        formLayout.add(orderId, customerId, objectDescription, pickupAdress, pickupCountry, deliveryAdress,
                deliveryCountry, vehicleId, etd, eta);

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

    private void populateForm(Order value) {
        this.order = value;
        binder.readBean(this.order);

    }
}
