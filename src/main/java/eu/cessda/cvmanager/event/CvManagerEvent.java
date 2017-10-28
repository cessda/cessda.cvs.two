package eu.cessda.cvmanager.event;

public class CvManagerEvent {
	
	public static enum EventType {
		
		CVSCHEME_CREATED(1),
		CVSCHEME_UPDATED(2),
		CVSCHEME_REMOVED(3),
		CVCONCEPT_ADD_DIALOG(10),
		CVCONCEPT_CREATED(11),
		CVCONCEPT_EDIT_MODE(12);
		
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
