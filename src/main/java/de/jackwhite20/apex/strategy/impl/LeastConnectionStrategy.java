/*
 * Copyright (c) 2016 "JackWhite20"
 *
 * This file is part of Apex.
 *
 * Apex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.jackwhite20.apex.strategy.impl;

import de.jackwhite20.apex.strategy.AbstractBalancingStrategy;
import de.jackwhite20.apex.util.BackendInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JackWhite20 on 26.06.2016.
 */
public class LeastConnectionStrategy extends AbstractBalancingStrategy {

    private Map<BackendInfo, Integer> connections = new ConcurrentHashMap<>();

    public LeastConnectionStrategy(List<BackendInfo> backend) {

        super(backend);

        for (BackendInfo target : backend) {
            connections.put(target, 0);
        }
    }

    @Override
    public synchronized BackendInfo selectBackend(String originHost, int originPort) {

        int least = Integer.MAX_VALUE;
        BackendInfo leastBackend = null;
        for (Map.Entry<BackendInfo, Integer> entry : connections.entrySet()) {
            if (entry.getValue() < least) {
                least = entry.getValue();
                leastBackend = entry.getKey();
            }
        }

        connections.put(leastBackend, connections.get(leastBackend) + 1);

        return leastBackend;
    }

    @Override
    public void disconnectedFrom(BackendInfo backendInfo) {

        connections.put(backendInfo, connections.get(backendInfo) - 1);
    }
}