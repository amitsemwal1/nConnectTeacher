package com.nconnect.teacher.model;

import java.util.List;

import com.nconnect.teacher.model.stories.ClassSectionList;

public interface TeacherCombinedClassSectionCallback {
    void teacherClassCombinedCallback(List<ClassSectionList> classSectionLists);
    void teacherClassCombinedErrorCallback(int responseCode, String responseMessage);
}
