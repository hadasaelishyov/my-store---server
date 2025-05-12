
package com.example.demo.service;

import com.example.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class UserService {
    @Autowired  // מי שמייצר את המופע של הקלאס זו הסביבה, אנחנו לא יודעות אפילו את השם של הקלאס שממש את הממשק הזה.
    protected UserRepo userRepo;

    public abstract boolean login(String email, String password) ;
}
