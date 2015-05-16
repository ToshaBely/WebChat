use chat;
select users.name, count(*) as count from messages join users on messages.user_id = users.id group by user_id having count >= 2;