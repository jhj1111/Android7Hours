package com.sesac.data.mapper

import com.sesac.common.utils.fixImageUrl
import com.sesac.data.dto.BreedDTO
import com.sesac.data.dto.PetDTO
import com.sesac.domain.model.Breed
import com.sesac.domain.model.Pet

fun Pet.toPetDTO() = PetDTO(
    id = id,
    owner = owner,
    name = name,
    gender = gender,
    birthday = birthday,
    neutering = neutering,
    breed = breed,
    image = image,
)

fun PetDTO.toPet() = Pet(
    id = id,
    owner = owner,
    name = name,
    gender = gender,
    birthday = birthday,
    neutering = neutering,
    breed = breed,
    image = fixImageUrl(image),
    linkedUser = linkedUser,
    lastLocation = lastLocation?.toDomain()
)

fun Breed.toBreedsDTO() = BreedDTO(
    id = this.id,
    breedName = this.breedName,
)

fun BreedDTO.toBreeds() = Breed(
    id = this.id,
    breedName = this.breedName,
)

fun List<Pet>.toPetDTOList() =
    this.map { it.toPetDTO() }.toList()

fun List<PetDTO>.toPetList() =
    this.map { it.toPet() }.toList()

fun List<BreedDTO>.toBreedsList() =
    this.map { it.toBreeds() }