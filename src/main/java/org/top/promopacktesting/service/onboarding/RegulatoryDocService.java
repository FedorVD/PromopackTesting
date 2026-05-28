package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.RegulatoryDoc;
import org.top.promopacktesting.repository.onboarding.RegulatoryDocRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RegulatoryDocService {

    @Autowired
    private RegulatoryDocRepository regulatoryDocRepository;

    public Optional<RegulatoryDoc> getByDocId(Long id) {
        return regulatoryDocRepository.findById(id);
    }

    public Optional<RegulatoryDoc> getRegulatoryDocByDocName(String regulatoryDocName) {
        return regulatoryDocRepository.findByDocName(regulatoryDocName);
    }

    public List<RegulatoryDoc> getAll() {
        return regulatoryDocRepository.findAll();
    }

    public RegulatoryDoc save(RegulatoryDoc regulatoryDoc) {
        return regulatoryDocRepository.save(regulatoryDoc);
    }

/*    public RegulatoryDoc createRegulatoryDoc(RegulatoryDoc regulatoryDoc) {
        return regulatoryDocRepository.save(regulatoryDoc);
    }*/

/*
    public void updateRegulatoryDoc(Long id, RegulatoryDoc regulatoryDoc) {
        Optional<RegulatoryDoc> existingDoc = regulatoryDocRepository.findBycId(id);
        if (existingDoc.isPresent()) {
            RegulatoryDoc existingRegulatoryDoc = existingDoc.get();
            existingRegulatoryDoc.setDocName(regulatoryDoc.getDocName());
            existingRegulatoryDoc.setDocUrl(regulatoryDoc.getDocUrl());
            regulatoryDocRepository.save(existingRegulatoryDoc);
        }
    }

    public void deleteRegulatoryDoc(Long id) {
        regulatoryDocRepository.deleteById(id);
    }
*/
}
