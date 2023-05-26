package com.example.application.views.fleetmanagement;

import com.example.application.data.entity.Vehicle;
import com.example.application.data.service.VehicleService;
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

@PageTitle("Fleetmanagement")
@Route(value = "fleetmanagement/:vehicleID?/:action?(edit)", layout = MainLayout.class)
public class FleetmanagementView extends Div implements BeforeEnterObserver {

    private final String VEHICLE_ID = "vehicleID";
    private final String VEHICLE_EDIT_ROUTE_TEMPLATE = "fleetmanagement/%s/edit";

    private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);

    private TextField vehicleId;
    private TextField vehicleName;
    private TextField vehicleType;
    private TextField vehicleMaxWeiht;
    private TextField vehicleMaxLength;
    private TextField vehicleMaxWidth;
    private TextField vehicleMaxHeight;
    private TextField vehicleComment;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Vehicle> binder;

    private Vehicle vehicle;

    private final VehicleService vehicleService;

    public FleetmanagementView(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
        addClassNames("fleetmanagement-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("vehicleId").setAutoWidth(true);
        grid.addColumn("vehicleName").setAutoWidth(true);
        grid.addColumn("vehicleType").setAutoWidth(true);
        grid.addColumn("vehicleMaxWeiht").setAutoWidth(true);
        grid.addColumn("vehicleMaxLength").setAutoWidth(true);
        grid.addColumn("vehicleMaxWidth").setAutoWidth(true);
        grid.addColumn("vehicleMaxHeight").setAutoWidth(true);
        grid.addColumn("vehicleComment").setAutoWidth(true);
        grid.setItems(query -> vehicleService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(VEHICLE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FleetmanagementView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Vehicle.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(vehicleId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("vehicleId");
        binder.forField(vehicleMaxWeiht).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("vehicleMaxWeiht");
        binder.forField(vehicleMaxLength).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("vehicleMaxLength");
        binder.forField(vehicleMaxWidth).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("vehicleMaxWidth");
        binder.forField(vehicleMaxHeight).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("vehicleMaxHeight");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.vehicle == null) {
                    this.vehicle = new Vehicle();
                }
                binder.writeBean(this.vehicle);
                vehicleService.update(this.vehicle);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(FleetmanagementView.class);
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
        Optional<Long> vehicleId = event.getRouteParameters().get(VEHICLE_ID).map(Long::parseLong);
        if (vehicleId.isPresent()) {
            Optional<Vehicle> vehicleFromBackend = vehicleService.get(vehicleId.get());
            if (vehicleFromBackend.isPresent()) {
                populateForm(vehicleFromBackend.get());
            } else {
                Notification.show(String.format("The requested vehicle was not found, ID = %s", vehicleId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FleetmanagementView.class);
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
        vehicleId = new TextField("Vehicle Id");
        vehicleName = new TextField("Vehicle Name");
        vehicleType = new TextField("Vehicle Type");
        vehicleMaxWeiht = new TextField("Vehicle Max Weiht");
        vehicleMaxLength = new TextField("Vehicle Max Length");
        vehicleMaxWidth = new TextField("Vehicle Max Width");
        vehicleMaxHeight = new TextField("Vehicle Max Height");
        vehicleComment = new TextField("Vehicle Comment");
        formLayout.add(vehicleId, vehicleName, vehicleType, vehicleMaxWeiht, vehicleMaxLength, vehicleMaxWidth,
                vehicleMaxHeight, vehicleComment);

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

    private void populateForm(Vehicle value) {
        this.vehicle = value;
        binder.readBean(this.vehicle);

    }
}
