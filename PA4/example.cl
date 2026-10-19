class Main inherits B {
 main() : Object { 0 };
};
class B inherits A { 
    n: Int <- 4;
    peer: IO;
    f() : Int { 2 };
};
class A { 
    title : String;
    ready : Bool;
    f() : Int { 1 };
    g() : Int { 3 };
};

