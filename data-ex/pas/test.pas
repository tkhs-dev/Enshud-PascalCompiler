program testBasic;
var a,b,c,d,e: integer;
bl: boolean;

begin
    a := 10;
    b := 20;
    c := a + b;
    d := a * b;
    e := (a + b) * (a - b);
    bl := true;
    writeln(c, d);
end.
