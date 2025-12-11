program testBasic;
var i: integer;
    c: char;

procedure subproc(x: integer; y: char);
var  c: char;
begin
    c := 's';
    writeln(c);
    writeln(x);
end;

begin
    c := 'm';
    subproc(c, 99);
    writeln(c);
end.
