package eu.cessda.cvmanager.service.es.entity;

import org.gesis.stardat.ddiflatdb.client.DDIStore;

public class DDIStoreBuilder {
	private DDIStoreES result;

	public DDIStoreBuilder() {

	}

	public DDIStoreBuilder(DDIStore ddiStore) {
		result = new DDIStoreES(ddiStore.getElementId());
		result.setContent(ddiStore.getContent());
		result.setElementOrder(ddiStore.getElementOrder());
		result.setStudy(ddiStore.getStudy());
		result.setType(ddiStore.getType());
		result.setPrimaryKey(ddiStore.getPrimaryKey());
	}
}
