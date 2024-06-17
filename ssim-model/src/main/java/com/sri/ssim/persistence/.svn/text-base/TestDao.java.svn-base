package com.sri.ssim.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/29/12
 */
public class TestDao {

    private EntityManagerFactory entityManagerFactory;

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        entityManagerFactory = emf;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Encounter getEncounterByName(String encounterName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Assert.notNull(entityManager);
        Encounter encounterEntity = null;

        Query query =
            entityManager
                .createQuery("select o FROM EncounterEntity o where o.name = :name");
        query.setParameter("name", encounterName);
        List<Encounter> encounterList = query.getResultList();
        if (!encounterList.isEmpty()) {
            encounterEntity = encounterList.get(0);
        }

        return encounterEntity;
    }

    @Transactional
    @NotNull
    public Encounter addEncounter(Encounter encounter) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Assert.notNull(entityManager);

        return entityManager.merge(encounter);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public List<Artifact> getArtifactsForProcessing() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Assert.notNull(entityManager);
        Query query =
            entityManager
                .createQuery("select o FROM ArtifactEntity o where o.analyzed = :analyzed");
        query.setParameter("analyzed", false);

        return query.getResultList();
    }

}
