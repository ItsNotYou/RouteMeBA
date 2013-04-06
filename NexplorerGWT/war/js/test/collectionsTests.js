describe("UpdatableCollection", function() {
	var sut;
	
	beforeEach(function() {
		sut = new UpdatableCollection();
		sut.update({"a" : "ale", "b" : "bail"});
	});
	
	it("saves the object on update", function() {
		sut.update({"t" : "tail", "a" : "andis"});
		expect(sut.current).toEqual({"t" : "tail", "a" : "andis"});
	});
	
	it("finds the new keys on update", function() {
		var result = sut.update({"t" : "tail", "a" : "andis"});
		expect(result.newKeys).toEqual(["t"]);
	});
	
	it("finds the same keys on update", function() {
		var result = sut.update({"t" : "tail", "a" : "andis"});
		expect(result.sameKeys).toEqual(["a"]);
	});
	
	it("finds the old keys on update", function() {
		var result = sut.update({"t" : "tail", "a" : "andis"});
		expect(result.oldKeys).toEqual(["b"]);
	});
});

describe("ObservableCollection", function() {
	var sut;
	var subscriber;
	
	beforeEach(function() {
		sut = new ObservableCollection();
		sut.update({"a" : "ale", "b" : "bail"});
		
		subscriber = new Object();
		subscriber.added = jasmine.createSpy("added");
		subscriber.remained = jasmine.createSpy("remained");
		subscriber.removed = jasmine.createSpy("removed");
		sut.subscribe(subscriber);
	});
	
	it("notifies subscribers of added values on update", function() {
		sut.update({"t" : "tail", "a" : "andis"});
		expect(subscriber.added).toHaveBeenCalledWith("t", "tail");
	});
	
	it("notifies subscribers of remained values on update", function() {
		sut.update({"t" : "tail", "a" : "andis"});
		expect(subscriber.remained).toHaveBeenCalledWith("a", "andis");
	});
	
	it("notifies subscribers of removed values on update", function() {
		sut.update({"t" : "tail", "a" : "andis"});
		expect(subscriber.removed).toHaveBeenCalledWith("b", "bail");
	});
});
