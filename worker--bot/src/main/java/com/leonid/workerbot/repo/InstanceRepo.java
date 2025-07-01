package com.leonid.workerbot.repo;

import com.leonid.workerbot.model.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanceRepo extends JpaRepository<Instance, String> {
    @Query("SELECT i FROM Instance i WHERE i.status = 'IDLE'")
    List<Instance> findReadyInstancesForUpdate();
}
