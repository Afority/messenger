package com.github.messenger.domain.value_objects;

import com.github.messenger.domain.exceptions.PasswordIsWeakException;
import lombok.Getter;

public class RawPassword {
    @Getter
    private String value;

    public RawPassword(String password) {
        if (isWeak(password)) throw new PasswordIsWeakException();
        value = password;
    }


    public static boolean isWeak(String password){
        boolean digits = false;
        boolean uppercase = false;
        boolean lowercase = false;

        // минимальные требования
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) uppercase = true;
            if (Character.isLowerCase(password.charAt(i))) lowercase = true;
            if (Character.isDigit(password.charAt(i))) digits = true;
            if (digits && uppercase && lowercase) break;
        }
        return password.length() < 8 || (!digits || !uppercase || !lowercase);
    }
}
