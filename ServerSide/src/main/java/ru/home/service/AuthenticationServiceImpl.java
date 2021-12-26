package ru.home.service;

import ru.home.service.interfaces.AuthenticationService;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationServiceImpl implements AuthenticationService {

    private List<UserEntity> userEntityList;

    public AuthenticationServiceImpl() {
        this.userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("grafwwwolf", "password", "grafwwwolf"));
        userEntityList.add(new UserEntity("robertrath", "password", "robertrath"));
        userEntityList.add(new UserEntity("markel", "password", "markel"));
    }

    @Override
    public void start() {
        System.out.println("Authentication service start");
    }

    @Override
    public void stop() {
        System.out.println("Authentication service stop");
    }

    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        for (UserEntity userEntity: userEntityList) {
            if (userEntity.login.equals(login) && userEntity.password.equals(password)) {
                return userEntity.nickName;
            }
        }
        return null;
    }

    private class UserEntity {
        private String login;
        private String password;
        private String nickName;

        public UserEntity(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }
    }
}
