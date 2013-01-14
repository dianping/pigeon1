namespace java com.dianping.dpsf.protocol.thrift

struct Customer {
	1: string name
	2: i32 age
}

exception ServiceException {
  1: i32 code,
  2: string message
}

service CustomerService {

	Customer getCustomer(1: i32 customerId) throws (1: ServiceException error)
	
	void createCustomer(1: Customer customer)  throws (1: ServiceException error)
	
}