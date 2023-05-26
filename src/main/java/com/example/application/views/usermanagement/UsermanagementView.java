package com.example.application.views.usermanagement;

import com.example.application.data.entity.RoleUser;
import com.example.application.data.service.RoleUserService;
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

@PageTitle("Usermanagement")
@Route(value = "usermanagement/:roleUserID?/:action?(edit)", layout = MainLayout.class)
public class UsermanagementView extends Div implements BeforeEnterObserver {

    private final String ROLEUSER_ID = "roleUserID";
    private final String ROLEUSER_EDIT_ROUTE_TEMPLATE = "usermanagement/%s/edit";

    private final Grid<RoleUser> grid = new Grid<>(RoleUser.class, false);

    private TextField userId;
    private TextField userName;
    private TextField userOccupation;
    private TextField userRole;
    private TextField userMail;
    private TextField userPhone;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<RoleUser> binder;

    private RoleUser roleUser;

    private final RoleUserService roleUserService;

    public UsermanagementView(RoleUserService roleUserService) {
        this.roleUserService = roleUserService;
        addClassNames("usermanagement-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("userId").setAutoWidth(true);
        grid.addColumn("userName").setAutoWidth(true);
        grid.addColumn("userOccupation").setAutoWidth(true);
        grid.addColumn("userRole").setAutoWidth(true);
        grid.addColumn("userMail").setAutoWidth(true);
        grid.addColumn("userPhone").setAutoWidth(true);
        grid.setItems(query -> roleUserService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ROLEUSER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(UsermanagementView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(RoleUser.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(userId).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("userId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.roleUser == null) {
                    this.roleUser = new RoleUser();
                }
                binder.writeBean(this.roleUser);
                roleUserService.update(this.roleUser);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(UsermanagementView.class);
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
        Optional<Long> roleUserId = event.getRouteParameters().get(ROLEUSER_ID).map(Long::parseLong);
        if (roleUserId.isPresent()) {
            Optional<RoleUser> roleUserFromBackend = roleUserService.get(roleUserId.get());
            if (roleUserFromBackend.isPresent()) {
                populateForm(roleUserFromBackend.get());
            } else {
                Notification.show(String.format("The requested roleUser was not found, ID = %s", roleUserId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(UsermanagementView.class);
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
        userId = new TextField("User Id");
        userName = new TextField("User Name");
        userOccupation = new TextField("User Occupation");
        userRole = new TextField("User Role");
        userMail = new TextField("User Mail");
        userPhone = new TextField("User Phone");
        formLayout.add(userId, userName, userOccupation, userRole, userMail, userPhone);

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

    private void populateForm(RoleUser value) {
        this.roleUser = value;
        binder.readBean(this.roleUser);

    }
}
