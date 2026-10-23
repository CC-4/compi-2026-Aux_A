class Main inherits B {
 main() : Object { new B };
};
class B inherits A { 
    n: Int <- 4;
    peer: IO <- new IO;
    f() : Int { 2 };
};
class A { 
    title : String <- "hola";
    ready : Bool;
    f() : Int { 1 };
    g() : Int { 3 };
};

