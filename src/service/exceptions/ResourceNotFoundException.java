package service.exceptions;

public class ResourceNotFoundException extends FactoryException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String msg) {
		super(msg);
	}
}
