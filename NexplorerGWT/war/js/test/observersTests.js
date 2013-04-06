describe("NeighbourObserver", function() {
	var sut;
	var subscriber;
	
	beforeEach(function() {
		sut = new NeighbourObserver();
		sut.added("1",
		{
			"neighbours":{"2":{"id":2}}
		});
		
		subscriber = new Object();
		subscriber.added = jasmine.createSpy("added");
		subscriber.remained = jasmine.createSpy("remained");
		subscriber.removed = jasmine.createSpy("removed");
		sut.subscribe(subscriber);
	});
	
	it("notifies of new node connections on added", function() {
		sut.added("2",
		{
			"neighbours":{"1":{"id":1}}
		});
		
		expect(subscriber.added).toHaveBeenCalledWith("2-1",
		{
			"from":
			{
				"neighbours":{"1":{"id":1}}
			},
			"to":
			{
				"id":1
			}
		});
	});
	
	it("notifies of removed node connections on removed", function() {
		sut.removed("1",
		{
			"neighbours":{"2":{"id":2}}
		});
		
		expect(subscriber.removed).toHaveBeenCalledWith("1-2",
		{
			"from":
			{
				"neighbours":{"2":{"id":2}}
			},
			"to":
			{
				"id":2
			}
		});
	});
	
	it("notifies of added neighbour connection on remained", function() {
		sut.remained("1",
		{
			"neighbours":{"3":{"id":3}}
		});
		
		expect(subscriber.added).toHaveBeenCalledWith("1-3",
		{
			"from":
			{
				"neighbours":{"3":{"id":3}}
			},
			"to":
			{
				"id":3
			}
		});
	});
	
	it("notifies of removed neighbour connection on remained", function() {
		sut.remained("1",
		{
			"neighbours":{"3":{"id":3}}
		});
		
		expect(subscriber.removed).toHaveBeenCalledWith("1-2",
		{
			"from":
			{
				"neighbours":{"2":{"id":2}}
			},
			"to":
			{
				"id":2
			}
		});
	});
	
	it("notifies of remained neighbour connection on remained", function() {
		sut.remained("1",
		{
			"neighbours":{"2":{"id":2}}
		});
		
		expect(subscriber.remained).toHaveBeenCalledWith("1-2",
		{
			"from":
			{
				"neighbours":{"2":{"id":2}}
			},
			"to":
			{
				"id":2
			}
		});
	});
});
