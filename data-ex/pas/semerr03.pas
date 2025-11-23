program testBasic;
var i: integer;
    c: char;

procedure subproc(x: integer; y: char);
var  c: char;
begin
    c := 's';
    write(c);
    write(x);
end;

begin
    c := 'm';
    subproc(c, 99);
    write(c);
end.
