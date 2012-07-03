namespace java com.dianping.dpsf.example.thrift

struct Parameter{

	1: string name
	2: i32 age
	3: list<string> book

}

struct ReturnValue{

	1: i32 money

}

exception InvalidOperation {
  1: i32 what,
  2: string why
}


service ExampleService{

	ReturnValue getMoney(1: Parameter parameter,2: string name) throws (1: InvalidOperation ouch)
	void test()
}