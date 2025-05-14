package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Course;
import com.test.demo.projections.dtos.CourseDto;
import com.test.demo.projections.dtos.SaveCourseDto;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "image", ignore = true)
    Course toCourse(SaveCourseDto saveCourseDto);
    
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "institution", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "prediction", ignore = true)
    @Mapping(target = "predictionAvgConfidence", ignore = true)
    CourseDto toCourseDto(Course course);
}
