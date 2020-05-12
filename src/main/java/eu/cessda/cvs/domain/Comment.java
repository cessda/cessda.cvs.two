package eu.cessda.cvs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.time.ZonedDateTime;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "info")
    private String info;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date_time")
    private ZonedDateTime dateTime;

    @ManyToOne
    @JsonIgnoreProperties("comments")
    private Version version;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public Comment content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Comment info(String info) {
        this.info = info;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Comment userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public Comment dateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Version getVersion() {
        return version;
    }

    public Comment version(Version version) {
        this.version = version;
        return this;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return id != null && id.equals(((Comment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", userId=" + getUserId() +
            ", dateTime='" + getDateTime() + "'" +
            "}";
    }
}
