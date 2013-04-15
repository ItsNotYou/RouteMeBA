describe("RandomRoutes", function () {
    var sut;

    beforeEach(function () {
        sut = new RandomRoutes();
    });

    it("finds an element", function () {
        var array = [
            {"from": 1, "to": 2}
        ];
        expect(sut.contains({"from": 1, "to": 2}, array)).toEqual(true);
    });

    it("creates a random route", function () {
        var nodes = {"1": "a", "2": "b"};
        expect(sut.createRoutes(nodes, 1).length).toEqual(1);
    });

    it("creates two random routes", function () {
        var nodes = {"1": "a", "2": "b"};
        expect(sut.createRoutes(nodes, 2)).toEqual([
            {"from": "1", "to": "2"},
            {"from": "2", "to": "1"}
        ]);
    });

    it("creates less routes if too much are asked", function () {
        var nodes = {"1": "a", "2": "b"};
        expect(sut.createRoutes(nodes, 10).length).toEqual(2);
    });
});
