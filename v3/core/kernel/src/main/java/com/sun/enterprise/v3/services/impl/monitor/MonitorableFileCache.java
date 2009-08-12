/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 */
package com.sun.enterprise.v3.services.impl.monitor;

import com.sun.grizzly.http.FileCache;

/**
 * Monitoring aware {@link FileCache} implementation.
 *
 * @author Alexey Stashok
 */
public class MonitorableFileCache extends FileCache {
    // The GrizzlyMonitoring objects, which encapsulates Grizzly probe emitters

    private final GrizzlyMonitoring grizzlyMonitoring;
    private final String fileCacheName;

    public MonitorableFileCache(GrizzlyMonitoring grizzlyMonitoring, String fileCacheName) {
        this.grizzlyMonitoring = grizzlyMonitoring;
        this.fileCacheName = fileCacheName;
    }

    @Override
    protected void recalcCacheStatsIfMonitoring(FileCacheEntry entry) {
        recalcCacheStats(entry);
    }
    
    @Override
    protected void countHit() {
        super.countHit();
        grizzlyMonitoring.getFileCacheProbeProvider().countHitEvent(fileCacheName);
    }

    @Override
    protected void countMiss() {
        super.countMiss();
        grizzlyMonitoring.getFileCacheProbeProvider().countMissEvent(fileCacheName);
    }

    @Override
    protected void countInfoHit() {
        super.countInfoHit();
        grizzlyMonitoring.getFileCacheProbeProvider().countInfoHitEvent(fileCacheName);
    }

    @Override
    protected void countInfoMiss() {
        super.countInfoMiss();
        grizzlyMonitoring.getFileCacheProbeProvider().countInfoMissEvent(fileCacheName);
    }

    @Override
    protected void countContentHit() {
        super.countContentHit();
        grizzlyMonitoring.getFileCacheProbeProvider().countContentHitEvent(fileCacheName);
    }

    @Override
    protected void countContentMiss() {
        super.countContentMiss();
        grizzlyMonitoring.getFileCacheProbeProvider().countContentMissEvent(fileCacheName);
    }

    @Override
    protected void incOpenCacheEntries() {
        super.incOpenCacheEntries();
        grizzlyMonitoring.getFileCacheProbeProvider().incOpenCacheEntriesEvent(
                fileCacheName);
    }

    @Override
    protected void decOpenCacheEntries() {
        super.decOpenCacheEntries();
        grizzlyMonitoring.getFileCacheProbeProvider().decOpenCacheEntriesEvent(
                fileCacheName);
    }

    @Override
    protected void addHeapSize(long size) {
        super.addHeapSize(size);
        grizzlyMonitoring.getFileCacheProbeProvider().addHeapSizeEvent(
                fileCacheName, size);
    }

    @Override
    protected void subHeapSize(long size) {
        super.subHeapSize(size);
        grizzlyMonitoring.getFileCacheProbeProvider().subHeapSizeEvent(
                fileCacheName, size);
    }

    @Override
    protected void addMappedMemorySize(long size) {
        super.addMappedMemorySize(size);
        grizzlyMonitoring.getFileCacheProbeProvider().addMappedMemorySizeEvent(
                fileCacheName, size);
    }

    @Override
    protected void subMappedMemorySize(long size) {
        super.subMappedMemorySize(size);
        grizzlyMonitoring.getFileCacheProbeProvider().subMappedMemorySizeEvent(
                fileCacheName, size);
    }
}
