package com.vetly.vetly_java.mapper;

import com.vetly.vetly_java.dto.AnimalRequest;
import com.vetly.vetly_java.dto.AnimalResponse;
import com.vetly.vetly_java.model.Animal;
import com.vetly.vetly_java.model.Especie;
import com.vetly.vetly_java.model.Sexo;
import com.vetly.vetly_java.model.Tutor;
import org.springframework.stereotype.Component;

@Component
public class AnimalMapper {

    public Animal animalRequestToAnimal(AnimalRequest request, Tutor tutor, Especie especie) {
        Animal animal = new Animal();
        animal.setNome(request.nome());
        animal.setRaca(request.raca());
        animal.setSexo(Sexo.valueOf(request.sexo().toUpperCase()));
        animal.setDataNascimento(request.dataNascimento());
        animal.setPeso(request.peso());
        animal.setTutor(tutor);
        animal.setEspecie(especie);
        animal.setUrlFoto(request.urlFoto());
        animal.setCastrado(request.castrado() ? "S" : "N");
        animal.setCondicoesPreexistentes(request.condicoesPreexistentes());
        animal.setAlergias(request.alergias());
        animal.setMedicacoesEmUso(request.medicacoesEmUso());
        return animal;
    }

    public void updateAnimalFromRequest(Animal animal, AnimalRequest request, Especie especie) {
        animal.setNome(request.nome());
        animal.setRaca(request.raca());
        animal.setSexo(Sexo.valueOf(request.sexo().toUpperCase()));
        animal.setDataNascimento(request.dataNascimento());
        animal.setPeso(request.peso());
        animal.setEspecie(especie);
        animal.setUrlFoto(request.urlFoto());
        animal.setCastrado(request.castrado() ? "S" : "N");
        animal.setCondicoesPreexistentes(request.condicoesPreexistentes());
        animal.setAlergias(request.alergias());
        animal.setMedicacoesEmUso(request.medicacoesEmUso());
    }

    public AnimalResponse animalToAnimalResponse(Animal animal) {
        return new AnimalResponse(
                animal.getId(),
                animal.getNome(),
                animal.getRaca(),
                animal.getSexo(),
                animal.getDataNascimento(),
                animal.getPeso(),
                animal.getEspecie().getNome().toString(),
                animal.getUrlFoto(),
                "S".equals(animal.getCastrado()),
                animal.getCondicoesPreexistentes(),
                animal.getAlergias(),
                animal.getMedicacoesEmUso()
        );
    }
}
