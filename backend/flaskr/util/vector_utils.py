import math

def euclidian_distance(vec1, vec2):
    size = len(vec1)
    vec_sum = 0.0
    for i in range(size):
        vec_sum += pow(vec1[i]-vec2[i], 2.0)

    return math.sqrt(vec_sum)

def avg_distance(vector_list):
    distance_sum = 0.0
    calc_cnt = 0
    for idx1 in range(len(vector_list)):
        for idx2 in range(idx1 + 1, len(vector_list)):
            distance_sum += euclidian_distance(vector_list[idx1], vector_list[idx2])
            calc_cnt += 1

    avg_distance = 0.0
    if len(vector_list) > 1:
        avg_distance = distance_sum / calc_cnt

    return avg_distance