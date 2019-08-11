package com.nconnect.teacher.model;

import com.nconnect.teacher.model.announcements.Announcement;
import com.nconnect.teacher.model.events.Event;
import com.nconnect.teacher.model.stories.Story;

public class TrendingStories {

    private Integer type;
    private Story story;
    private Result issues;
    private Announcement announcement;
    private Event event;

    public TrendingStories(Integer type, Story story, Result issues, Announcement announcement, Event event) {
        this.type = type;
        this.story = story;
        this.issues = issues;
        this.announcement = announcement;
        this.event = event;
    }

    public TrendingStories() {

    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Result getIssues() {
        return issues;
    }

    public void setIssues(Result issues) {
        this.issues = issues;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
