package org.top.promopacktesting.model.onboarding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Сущность записи справочника нормативных документов
@Entity
@Table(name = "regulatory_doc")
@NoArgsConstructor
@Getter
@Setter
public class RegulatoryDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "doc_name")
    private String docName;

    @Column (name = "doc_url")
    private String docUrl;
}
