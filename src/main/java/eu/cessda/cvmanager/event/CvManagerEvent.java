package eu.cessda.cvmanager.event;

public class CvManagerEvent {
	
	public static enum EventType {
		
		CVSCHEME_CREATED(1),
		CVSCHEME_UPDATED(2),
		CVSCHEME_REMOVED(3),
		CVCONCEPT_ADD_DIALOG(10),
		CVCONCEPT_TRANSLATION_DIALOG(11),
		CVCONCEPT_CREATED(12),
		CVCONCEPT_EDIT_MODE(13), 
		CVCONCEPT_ADDCHILD_DIALOG(14), 
		CVCONCEPT_DELETED(15),
		CVCONCEPT_SORT(16),
		AGENCY_SEARCH_MODE(50),
		AGENCY_MANAGE_MEMBER(51),
		VOCABULARY_SEARCH(100),
		VOCABULARY_EDITOR_SEARCH(200);
		
		private final int value;
		
		private EventType( int value ) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public static class Event {
		private final EventType type;
		private final Object payload;
		
		public Event(EventType type, Object payload) {
			this.type = type;
			this.payload = payload;
		}

		public EventType getType() {
			return type;
		}

		public Object getPayload() {
			return payload;
		}
	}
}
