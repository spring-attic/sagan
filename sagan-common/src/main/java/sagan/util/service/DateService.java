package sagan.util.service;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateService {
    public Date now() {
        return new Date();
    }
}
