package ru.itmo.server.managers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.exceptions.DuplicateIdException;
import ru.itmo.common.models.Ticket;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages collection
 */
public class CollectionManager {
    private final static Logger logger = LogManager.getLogger(CollectionManager.class);
    private final Set<Long> usedIds = new HashSet<>();
    private final LocalDateTime initTime;
    private final Set<Ticket> collection = new HashSet<>();

    private final ReentrantLock lock = new ReentrantLock();

    public CollectionManager() {
        this.initTime = LocalDateTime.now();
    }

    /**
     * Add ticket to collection if its unique
     *
     * @param ticket ticket to add
     * @throws DuplicateIdException if id already used
     */
    public void add(Ticket ticket) throws DuplicateIdException {
        lock.lock();
        try {
            final long id = ticket.getId();
            if (usedIds.contains(id)) {
                throw new DuplicateIdException(id);
            }
            usedIds.add(id);
            collection.add(ticket);
        } finally {
            lock.unlock();
        }
    }


    public int getCollectionSize() {
        lock.lock();
        try {
            return collection.size();
        } finally {
            lock.unlock();
        }
    }

    public Set<Ticket> getCollection() {
        lock.lock();
        try {
            return new HashSet<>(collection);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set collection
     *
     * @param newCollection collection to add
     */
    public void setCollection(Set<Ticket> newCollection) {
        lock.lock();
        try {
            collection.clear();
            usedIds.clear();
            long maxId = 0;
            for (Ticket ticket : newCollection) {
                try {
                    add(ticket);
                    maxId = Math.max(maxId, ticket.getId());
                } catch (DuplicateIdException e) {
                    logger.warn("Duplicate id detected: {}", e.getMessage());
                }
            }
            Ticket.setIdCounter(maxId + 1);
        } finally {
            lock.unlock();
        }
    }

    public void deleteAllByUser(String login) {
        lock.lock();
        try {
            collection.removeIf(ticket -> ticket.getOwner().equals(login));
        } finally {
            lock.unlock();
        }
    }

    public LocalDateTime getInitTime() {
        return this.initTime;
    }

    public String getCollectionType() {
        lock.lock();
        try {
            return this.collection.getClass().getSimpleName();
        } finally {
            lock.unlock();
        }
    }
}
