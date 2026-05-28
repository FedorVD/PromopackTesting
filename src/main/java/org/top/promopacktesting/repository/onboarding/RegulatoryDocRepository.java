package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;
import org.top.promopacktesting.model.onboarding.RegulatoryDoc;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegulatoryDocRepository extends JpaRepository<RegulatoryDoc, Long> {

    Optional<RegulatoryDoc> findById(Long id);
    Optional<RegulatoryDoc> findByDocName(String docName);
    List<RegulatoryDoc> findAll();

/*
    RegulatoryDoc create(RegulatoryDoc regulatoryDoc);
    RegulatoryDoc update(RegulatoryDoc regulatoryDoc);
    void deleteById(Long id);
    RegulatoryDoc save(RegulatoryDoc regulatoryDoc);
*/
}
