package co.mapoteca.service;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PointInformationService {

    private ModelMapper modelMapper;

    public PointInformationService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
