program testBasic;
var i: integer;
    c: char;

procedure subproc;
var  c: char;
begin
    c := 's';
    write(c);
end;

begin
    c := 'm';
    subproc;
    write(c);
end.
