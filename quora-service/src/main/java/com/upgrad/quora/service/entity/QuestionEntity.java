package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "question")
@NamedQueries(
        {@NamedQuery(name = "getAllQuestions", query = "select qs from QuestionEntity qs"),
                @NamedQuery(name = "questionByUuid", query = "select qs from QuestionEntity qs where qs.uuid = :uuid"),
                @NamedQuery(name = "getAllQuestionByUUID", query = "select qs from QuestionEntity qs where qs.user.id = :user_id")
        }
)

public class QuestionEntity implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 250)
    private String uuid;

    @Column(name = "CONTENT")
    @Size(max = 500)
    private String content;

    @Column(name = "DATE")
    private ZonedDateTime date;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser_id() {
        return user;
    }

    public void setUser_id(UserEntity user_id) {
        this.user = user_id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}