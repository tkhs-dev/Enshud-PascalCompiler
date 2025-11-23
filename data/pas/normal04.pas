program testBasic;
var i: integer;
    a: array[1..10] of integer;

begin
    i := 11;
    a[5] := i;
    write(a[5]);
end.
