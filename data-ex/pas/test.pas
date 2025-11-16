program testBasic;
var i: integer;
    c1: char;

procedure subproc(x: integer);
var  c2: char;
begin
    c2 := 's';
    writeln(c2);
    writeln(x);
end;

begin
    i := i + 1;
    c1 := 'm';
    subproc(99);
    writeln(c1);
end.
