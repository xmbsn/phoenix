package com.dianping.phoenix.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.console.dal.deploy.VersionDao;
import com.dianping.phoenix.console.dal.deploy.VersionEntity;

public class DefaultVersionManager implements VersionManager {
	private static final String KERNEL = "kernel";

	@Inject
	private StatusReporter m_reporter;

	@Inject
	private WarService m_warService;

	@Inject
	private GitService m_gitService;

	@Inject
	private VersionDao m_dao;

	@Override
	public Version createVersion(String version, String description,
			String releaseNotes, String createdBy) throws Exception {
		
		
		
		return store(version, description, releaseNotes, createdBy);
	}

	@Override
	public List<Version> getActiveVersions() throws Exception {
		List<Version> versions = m_dao.findAllActive(KERNEL,
				VersionEntity.READSET_FULL);

		// order in descend
		Collections.sort(versions, new Comparator<Version>() {
			@Override
			public int compare(Version v1, Version v2) {
				return v2.getVersion().compareTo(v1.getVersion());
			}
		});

		return versions;
	}

	@Override
	public void removeVersion(int id) throws Exception {

		try {
			Version proto = m_dao.findByPK(id, VersionEntity.READSET_FULL);

			m_gitService.removeTag(proto.getVersion());

			if (proto.getStatus() == 0) {
				proto.setStatus(1);
				m_dao.updateByPK(proto, VersionEntity.UPDATESET_FULL);
			}
		} catch (DalNotFoundException e) {
			// ignore it
		}
	}

	private Version store(String version, String description,
			String releaseNotes, String createdBy) throws DalException {
		try {
			m_dao.findByDomainVersion(KERNEL, version,
					VersionEntity.READSET_FULL);

			m_reporter.log(String.format(
					"Kernel version(%s) is already existed!", version));
		} catch (DalNotFoundException e) {
			// expected
		}

		Version proto = m_dao.createLocal();

		proto.setDomain(KERNEL);
		proto.setVersion(version);
		proto.setDescription(description);
		proto.setReleaseNotes(releaseNotes);
		proto.setCreatedBy(createdBy);
		proto.setStatus(0);

		m_dao.insert(proto);
		return proto;
	}

	@Override
	public String getStatus() {
		
		
		return null;
	}

}
