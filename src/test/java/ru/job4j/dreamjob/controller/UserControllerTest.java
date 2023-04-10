package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private HttpSession httpSession;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        httpServletRequest = mock(HttpServletRequest.class);
        httpSession = mock(HttpSession.class);
    }

    @Test
    void getRegistrationPage() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void register() {
        var user = new User(1, "email", "name", "password");
       when(userService.save(user)).thenReturn(Optional.of(user));

       var model = new ConcurrentModel();
       var view = userController.register(model, user);

       assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void exceptionThenRegister() {
        var user = new User(1, "email", "name", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualError = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualError).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    void getLoginPage() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void loginUser() {
        var user = new User(1, "email", "name", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(user));
        when(httpServletRequest.getSession()).thenReturn(httpSession);

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, httpServletRequest);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void logout() {
        var view = userController.logout(httpSession);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}