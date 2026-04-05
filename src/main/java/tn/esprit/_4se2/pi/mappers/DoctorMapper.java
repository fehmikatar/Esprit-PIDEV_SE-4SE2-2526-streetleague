package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Doctor.DoctorRequest;
import tn.esprit._4se2.pi.dto.Doctor.DoctorResponse;
import tn.esprit._4se2.pi.entities.Doctor;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequest request) {
        if (request == null) return null;

        Doctor doctor = new Doctor();
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setAddress(request.getAddress());
        return doctor;
    }

    public DoctorResponse toResponse(Doctor entity) {
        if (entity == null) return null;

        return DoctorResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .specialty(entity.getSpecialty())
                .licenseNumber(entity.getLicenseNumber())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .build();
    }

    public void updateEntity(DoctorRequest request, Doctor doctor) {
        if (request == null || doctor == null) return;

        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setAddress(request.getAddress());
    }
}