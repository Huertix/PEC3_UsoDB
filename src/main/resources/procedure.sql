
CREATE OR REPLACE FUNCTION list_top_toys_children()
RETURNS SETOF ChildList AS $$
DECLARE
	output ChildList;
BEGIN

	FOR output IN
		SELECT c.child_id, c.child_name, c.city,
						SUM(l.number_toys) AS total_number,
						COUNT(l.child_id) as total_letters,
						MAX(l.number_toys) as max_toys_req_per_letter
			FROM CHILD c JOIN LETTER l ON c.child_id = l.child_id
		GROUP BY c.child_id
		ORDER BY total_number DESC,  c.child_name ASC
		LIMIT 10 LOOP

		output.toy_id:= (
			SELECT toy_id FROM (
    			SELECT w.toy_id, COUNT(w.toy_id) as toy_id_most_requested
					FROM letter l JOIN WISHED_TOY w ON l.letter_id = w.letter_id
    				WHERE l.child_id = output.child_id
				GROUP BY toy_id
				ORDER BY toy_id_most_requested DESC, toy_id ASC
				LIMIT 1
			) as q
		);

	RETURN NEXT output;
END LOOP;

EXCEPTION
	WHEN raise_exception THEN
		RAISE EXCEPTION 'ERROR: List not available';

RETURN;
END
$$LANGUAGE plpgsql;
