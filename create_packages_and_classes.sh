#!/bin/bash
# base package directory
baseDir="src/main/java/com/transactease/secureweather"

# create package directories
mkdir -p $baseDir/{model,dto,service,controller,repository,utils,client,mappers,configuration}

# create empty Java classes
touch $baseDir/model/User.java
touch $baseDir/model/City.java
touch $baseDir/dto/UserDto.java
touch $baseDir/dto/CityDto.java
touch $baseDir/service/UserService.java
touch $baseDir/service/CityService.java
touch $baseDir/controller/UserController.java
touch $baseDir/controller/CityController.java
touch $baseDir/repository/UserRepository.java
touch $baseDir/repository/CityRepository.java
