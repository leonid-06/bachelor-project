package com.leonid.workerbot.service;

import com.leonid.model.ApplicationStack;
import com.leonid.workerbot.model.Instance;
import com.leonid.workerbot.repo.InstanceRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanceManageService {
    private final InstanceRepo repo;

    public List<Instance> getInstances() {
        return repo.findAll();
    }

    public Instance create(String instanceId, ApplicationStack stack) {
        Instance instance = new Instance(instanceId, stack);
        return repo.save(instance);
    }

    public void update(Instance instance) {
        repo.save(instance);
    }

}
