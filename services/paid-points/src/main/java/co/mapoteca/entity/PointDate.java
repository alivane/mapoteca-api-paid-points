package co.mapoteca.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class PointDate {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean processed;

    @NonNull
    @Column(nullable = false)
    private Integer range;

    @Column(nullable = false)
    private Integer day;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Basic(fetch = FetchType.LAZY)
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object pointInformation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PointDate pointDate = (PointDate) o;
        return id != null && Objects.equals(id, pointDate.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Transient
    @Getter(AccessLevel.NONE)
    LocalDate fullPointDate;

    public LocalDate getFullPointDate() {
        String date = this.day + "/" + this.month + "/" + this.year;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
