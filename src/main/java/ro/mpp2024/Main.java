package ro.mpp2024;

import ro.mpp2024.model.Office;
import ro.mpp2024.repository.OfficeRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        Properties props=new Properties();
        try {
            props.load(new FileReader("db.config"));
        } catch (IOException e) {
            System.out.println("Cannot find db.config "+e);
        }

        OfficeRepository officeRepo=new OfficeRepository(props);
        officeRepo.findAll();
        for(Office office:officeRepo.findAll())
            System.out.println(office);
    }
}